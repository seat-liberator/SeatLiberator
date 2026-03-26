package com.seatliberator.seatliberator.eventrelay.core.relay;

import com.seatliberator.seatliberator.eventrelay.core.codec.EventPayloadDeserializer;
import com.seatliberator.seatliberator.eventrelay.core.definition.EventDefinition;
import com.seatliberator.seatliberator.eventrelay.core.model.EventEnvelope;
import com.seatliberator.seatliberator.eventrelay.core.model.EventPayload;
import com.seatliberator.seatliberator.eventrelay.core.model.EventTrace;
import com.seatliberator.seatliberator.eventrelay.core.model.EventType;
import com.seatliberator.seatliberator.eventrelay.core.relay.fixture.FakeEventListener;
import com.seatliberator.seatliberator.eventrelay.core.relay.fixture.FakeEventTraceHolder;
import com.seatliberator.seatliberator.eventrelay.core.relay.inbound.EventListenerRegistry;
import com.seatliberator.seatliberator.eventrelay.core.relay.inbound.RegistryEventRouter;
import com.seatliberator.seatliberator.eventrelay.core.util.EventTestFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@DisplayName("Registry Event Router")
public class RegistryEventRouterTest {

    @Test
    @DisplayName("이벤트 타입에 맞는 리스너를 찾아 payload를 역직렬화하고 handler를 호출한다")
    void route() {
        var type = eventType("event.created");
        var envelope = envelope(type, "raw-payload");
        var payload = EventTestFixture.createPayload();

        var definition = definition(type, EventTestFixture.TestPayload.class);
        var listener = new FakeEventListener(definition);

        var listenerRegistry = mock(EventListenerRegistry.class);
        var payloadDeserializer = mock(EventPayloadDeserializer.class);
        var traceHolder = new FakeEventTraceHolder();

        doReturn(listener).when(listenerRegistry).resolve(type);
        given(payloadDeserializer.materialize("raw-payload", EventTestFixture.TestPayload.class)).willReturn(payload);

        var router = new RegistryEventRouter(listenerRegistry, payloadDeserializer, traceHolder);

        router.route(envelope);

        assertThat(listener.lastHandledEnvelope()).isSameAs(envelope);
        assertThat(listener.lastHandledPayload()).isSameAs(payload);

        verify(listenerRegistry).resolve(type);
        verify(payloadDeserializer).materialize("raw-payload", EventTestFixture.TestPayload.class);
    }

    @Test
    @DisplayName("리스너가 없으면 예외가 발생한다")
    void missingListener() {
        var type = eventType("event.missing");
        var envelope = envelope(type, "raw-payload");

        var listenerRegistry = mock(EventListenerRegistry.class);
        var payloadDeserializer = mock(EventPayloadDeserializer.class);
        var traceHolder = new FakeEventTraceHolder();

        given(listenerRegistry.resolve(type)).willReturn(null);

        var router = new RegistryEventRouter(listenerRegistry, payloadDeserializer, traceHolder);

        assertThatThrownBy(() -> router.route(envelope))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Missing listener");

        verify(listenerRegistry).resolve(type);
        verifyNoInteractions(payloadDeserializer);
    }

    @Test
    @DisplayName("source trace를 바인딩한 상태로 handler를 실행하고 종료 후 상태를 복원한다")
    void route_bindsSourceTrace() {
        var type = eventType("event.created");
        var envelope = envelope(type, "raw-payload");
        var payload = EventTestFixture.createPayload();

        var definition = definition(type, EventTestFixture.TestPayload.class);
        var listener = new FakeEventListener(definition);

        var listenerRegistry = mock(EventListenerRegistry.class);
        var payloadDeserializer = mock(EventPayloadDeserializer.class);
        var traceHolder = new FakeEventTraceHolder();

        doReturn(listener).when(listenerRegistry).resolve(type);
        given(payloadDeserializer.materialize("raw-payload", EventTestFixture.TestPayload.class)).willReturn(payload);

        var router = new RegistryEventRouter(listenerRegistry, payloadDeserializer, traceHolder);

        assertThat(traceHolder.current().sourceTrace()).isNull();

        router.route(envelope);

        assertThat(listener.traceInHandler()).isSameAs(envelope.trace());
        assertThat(traceHolder.current().sourceTrace()).isNull();
    }

    private EventType eventType(String name) {
        var type = mock(EventType.class);
        given(type.name()).willReturn(name);
        return type;
    }

    private EventEnvelope envelope(EventType type, String rawPayload) {
        var envelope = mock(EventEnvelope.class, RETURNS_DEEP_STUBS);
        var trace = mock(EventTrace.class);

        given(envelope.header().eventType()).willReturn(type);
        given(envelope.rawPayload()).willReturn(rawPayload);
        given(envelope.trace()).willReturn(trace);

        return envelope;
    }

    @SuppressWarnings("unchecked")
    private <P extends EventPayload> EventDefinition<P> definition(EventType type, Class<P> payloadType) {
        var definition = mock(EventDefinition.class);
        given(definition.type()).willReturn(type);
        given(definition.payloadType()).willReturn(payloadType);
        return definition;
    }
}
