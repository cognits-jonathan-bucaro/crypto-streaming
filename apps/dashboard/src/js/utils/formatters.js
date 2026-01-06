export function formatPrice(price) {
    return new Intl.NumberFormat('en-US', {
        style: 'currency',
        currency: 'USD',
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
    }).format(price);
}

export function formatChange(change) {
    const prefix = change >= 0 ? '+' : '';
    return prefix + new Intl.NumberFormat('en-US', {
        style: 'currency',
        currency: 'USD',
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
    }).format(change);
}

export function formatPercent(percent) {
    const prefix = percent >= 0 ? '+' : '';
    return prefix + (percent * 100).toFixed(2) + '%';
}

export function formatTime(isoTimestamp) {
    const date = new Date(isoTimestamp);
    return date.toLocaleTimeString('en-US', {
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit',
        hour12: false
    });
}

export function getTrendIcon(trend) {
    switch (trend) {
        case 'up':
            return '↑';
        case 'down':
            return '↓';
        default:
            return '→';
    }
}

export function getTrendColor(trend) {
    switch (trend) {
        case 'up':
            return 'text-green-500';
        case 'down':
            return 'text-red-500';
        default:
            return 'text-gray-500';
    }
}
