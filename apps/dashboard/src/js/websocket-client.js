import { RetryStrategy } from './utils/retry-strategy.js';
import { HEARTBEAT_INTERVAL } from './utils/constants.js';

class WebSocketClient {
    constructor(url, handlers) {
        this.url = url;
        this.handlers = handlers;
        this.retryStrategy = new RetryStrategy({
            maxRetries: 10,
            baseDelay: 1000,
            maxDelay: 30000,
            factor: 2
        });
    }

    connect() {
        this.ws = new WebSocket(this.url);

        this.ws.onopen = () => {
            this.retryStrategy.reset();
            this.startHeartbeat();
            this.handlers.onConnect?.();
        };

        this.ws.onclose = (event) => {
            this.stopHeartbeat();
            this.handlers.onDisconnect?.();
            this.scheduleReconnect();
        };

        this.ws.onmessage = (event) => {
            this.handlers.onMessage?.(event.data);
        };
    }

    scheduleReconnect() {
        const delay = this.retryStrategy.nextDelay();
        if (delay !== null) {
            setTimeout(() => this.connect(), delay);
        }
    }

    send(message) {
        if (this.ws && this.ws.readyState === WebSocket.OPEN) {
            this.ws.send(JSON.stringify(message));
        }
    }

    startHeartbeat() {
        this.heartbeatInterval = setInterval(() => {
            this.send({ action: 'ping' });
        }, HEARTBEAT_INTERVAL);
    }

    stopHeartbeat() {
        if (this.heartbeatInterval) {
            clearInterval(this.heartbeatInterval);
            this.heartbeatInterval = null;
        }
    }

    disconnect() {
        this.stopHeartbeat();
        if (this.ws) {
            this.ws.close();
        }
    }
}

export { WebSocketClient };