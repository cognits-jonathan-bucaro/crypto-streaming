import { WebSocketClient } from './websocket-client.js';
import { PriceService } from './services/price-service.js';
import { PriceCard } from './components/price-card.js';
import { ConnectionStatus } from './components/connection-status.js';
import { WS_URL } from './utils/constants.js';

document.addEventListener('DOMContentLoaded', () => {
    const connectionStatusContainer = document.getElementById('connection-status');
    const priceCardsContainer = document.getElementById('price-cards');

    const connectionStatus = new ConnectionStatus(connectionStatusContainer);

    const wsClient = new WebSocketClient(WS_URL, {
        onConnect: () => {
            console.log('WebSocket connected');
            connectionStatus.setConnected();
        },
        onDisconnect: () => {
            console.log('WebSocket disconnected');
            connectionStatus.setDisconnected();
        },
        onMessage: (data) => {
            try {
                const message = JSON.parse(data);
                priceService.handleMessage(message);
            } catch (error) {
                console.error('Error parsing message:', error);
            }
        }
    });

    const priceService = new PriceService(wsClient);

    const priceCards = new Map();

    priceService.addListener((symbolCode, data, previousData) => {
        let card = priceCards.get(symbolCode);
        if (!card) {
            // Dynamically create card for new symbol
            card = new PriceCard({ code: symbolCode }, priceService);
            priceCardsContainer.appendChild(card.element);
            priceCards.set(symbolCode, card);
        }
        card.update(data, previousData);
    });

    wsClient.connect();

    window.addEventListener('beforeunload', () => {
        wsClient.disconnect();
    });
});
