class ConnectionStatus {
    constructor(container) {
        this.container = container;
        this.element = this.render();
        this.container.appendChild(this.element);
    }

    render() {
        const wrapper = document.createElement('div');
        wrapper.className = 'flex items-center gap-2';
        wrapper.innerHTML = `
            <div class="status-dot w-3 h-3 rounded-full bg-gray-500"></div>
            <span class="status-text text-sm font-medium text-gray-700">Connecting...</span>
        `;
        return wrapper;
    }

    setConnected() {
        const dot = this.element.querySelector('.status-dot');
        const text = this.element.querySelector('.status-text');
        dot.className = 'status-dot w-3 h-3 rounded-full bg-green-500';
        text.className = 'status-text text-sm font-medium text-green-700';
        text.textContent = 'Connected';
    }

    setDisconnected() {
        const dot = this.element.querySelector('.status-dot');
        const text = this.element.querySelector('.status-text');
        dot.className = 'status-dot w-3 h-3 rounded-full bg-red-500';
        text.className = 'status-text text-sm font-medium text-red-700';
        text.textContent = 'Disconnected';
    }

    setReconnecting(attempt) {
        const dot = this.element.querySelector('.status-dot');
        const text = this.element.querySelector('.status-text');
        dot.className = 'status-dot w-3 h-3 rounded-full bg-yellow-500 status-pulse';
        text.className = 'status-text text-sm font-medium text-yellow-700';
        text.textContent = attempt ? `Reconnecting (attempt ${attempt})...` : 'Reconnecting...';
    }
}

export { ConnectionStatus };
