/**
 * Configurations for LifecycleListeners. These are registered as soon as client started.
 */
export declare class ListenerConfig {
    lifecycle: Function[];
    addLifecycleListener(listener: Function): void;
    getLifecycleListeners(): Function[];
}
