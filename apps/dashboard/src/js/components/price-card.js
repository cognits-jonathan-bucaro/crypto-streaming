import { formatPrice, formatChange, formatPercent, formatTime, getTrendIcon, getTrendColor } from '../utils/formatters.js';

class PriceCard {
    constructor(symbolData, priceService) {
        this.symbolData = symbolData;
        this.priceService = priceService;
        // Don't subscribe by default - empty subscriptions receive all updates
        this.subscribed = false;
        this.element = this.render();
        this.attachEventListeners();
    }

    render() {
        const card = document.createElement('div');
        card.className = 'price-card bg-white rounded-lg shadow-lg p-6 transition-all duration-300';
        card.innerHTML = `
            <div class="flex justify-between items-start mb-4">
                <div>
                    <h2 class="text-2xl font-bold text-gray-800">${this.symbolData.code}</h2>
                    <p class="text-sm text-gray-500">${this.symbolData.name || this.symbolData.code}</p>
                </div>
                <span class="px-3 py-1 rounded text-sm font-medium bg-green-100 text-green-700 border border-green-300">
                    Live
                </span>
            </div>

            <div class="mb-4">
                <div class="price-value text-3xl font-bold text-gray-900">$0.00</div>
            </div>

            <div class="flex justify-between items-center mb-2">
                <div class="flex items-center gap-2">
                    <span class="change-value text-gray-500 font-semibold">$0.00</span>
                    <span class="change-percent text-gray-500">0.00%</span>
                </div>
                <div class="trend-indicator text-2xl text-gray-500">→</div>
            </div>

            <div class="text-xs text-gray-400 mt-2">
                <span class="timestamp">--:--:--</span>
            </div>
        `;
        return card;
    }

    attachEventListeners() {
        // No interactive elements currently
    }

    update(data, previousData) {
        const priceEl = this.element.querySelector('.price-value');
        const changeEl = this.element.querySelector('.change-value');
        const percentEl = this.element.querySelector('.change-percent');
        const trendEl = this.element.querySelector('.trend-indicator');
        const timestampEl = this.element.querySelector('.timestamp');

        priceEl.textContent = formatPrice(data.price);
        changeEl.textContent = formatChange(data.change);
        percentEl.textContent = formatPercent(data.changePercent);
        trendEl.textContent = getTrendIcon(data.trend);
        timestampEl.textContent = formatTime(data.timestamp);

        const trendColorClass = getTrendColor(data.trend);
        changeEl.className = `change-value font-semibold ${trendColorClass}`;
        percentEl.className = `change-percent ${trendColorClass}`;
        trendEl.className = `trend-indicator text-2xl ${trendColorClass}`;

        if (previousData && data.price !== previousData.price) {
            this.flashCard(data.trend);
        }
    }

    flashCard(trend) {
        this.element.classList.remove('flash-up', 'flash-down');
        void this.element.offsetWidth;

        if (trend === 'up') {
            this.element.classList.add('flash-up');
        } else if (trend === 'down') {
            this.element.classList.add('flash-down');
        }
    }

}

export { PriceCard };
