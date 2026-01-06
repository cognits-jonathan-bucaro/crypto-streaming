class RetryStrategy {
    constructor(options = {}) {
        this.maxRetries = options.maxRetries || 10;
        this.baseDelay = options.baseDelay || 1000;
        this.maxDelay = options.maxDelay || 30000;
        this.factor = options.factor || 2;
        this.attempts = 0;
    }

    nextDelay() {
        if (this.attempts >= this.maxRetries) {
            return null;
        }

        const delay = Math.min(
            this.baseDelay * Math.pow(this.factor, this.attempts),
            this.maxDelay
        );

        this.attempts++;
        return delay;
    }

    reset() {
        this.attempts = 0;
    }

    getAttempts() {
        return this.attempts;
    }
}

export { RetryStrategy };
