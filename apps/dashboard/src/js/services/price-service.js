class PriceService {
    constructor(wsClient) {
        this.wsClient = wsClient;
        this.prices = new Map();
        this.subscriptions = new Set();
        this.listeners = [];
    }

    handleMessage(message) {
        if (message.type === 'priceUpdate') {
            this.updatePrice(message.payload);
        } else if (message.type === 'welcome') {
            console.log('Connected to WebSocket:', message.sessionId);
        }
    }

    updatePrice(payload) {
        const previousData = this.prices.get(payload.symbol);
        this.prices.set(payload.symbol, payload);

        // Notify listeners for all updates - cards will be created dynamically
        // The subscription mechanism allows users to filter which symbols they want to see
        this.notifyListeners(payload.symbol, payload, previousData);
    }

    subscribe(symbol) {
        this.wsClient.send({ action: 'subscribe', symbol });
        this.subscriptions.add(symbol);
        console.log(`Subscribed to ${symbol}`);
    }

    unsubscribe(symbol) {
        this.wsClient.send({ action: 'unsubscribe', symbol });
        this.subscriptions.delete(symbol);
        console.log(`Unsubscribed from ${symbol}`);
    }

    isSubscribed(symbol) {
        return this.subscriptions.has(symbol);
    }

    getPrice(symbol) {
        return this.prices.get(symbol);
    }

    addListener(callback) {
        this.listeners.push(callback);
    }

    notifyListeners(symbol, data, previousData) {
        this.listeners.forEach(cb => cb(symbol, data, previousData));
    }
}

export { PriceService };
