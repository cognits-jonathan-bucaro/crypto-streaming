class PriceTable {
    updatePrice(data) {
        const row = this.rows.get(data.symbol);

        // Update cells
        this.setCell(row, 'price', formatPrice(data.price));
        this.setCell(row, 'change', formatChange(data.change),
            this.getChangeClass(data.change));
        this.setCell(row, 'changePercent', formatPercent(data.changePercent),
            this.getChangeClass(data.change));
        this.setCell(row, 'trend', this.createTrendIndicator(data.trend));
        this.setCell(row, 'timestamp', formatTime(data.timestamp));

        // Flash animation
        this.flashRow(row, data.trend);
    }

    flashRow(row, trend) {
        row.classList.remove('flash-up', 'flash-down');
        void row.offsetWidth; // Force reflow
        if (trend === 'up') {
            row.classList.add('flash-up');
        } else if (trend === 'down') {
            row.classList.add('flash-down');
        }
    }
}