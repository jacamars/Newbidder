import { ConnectionHeartbeatListener } from './core/ConnectionHeartbeatListener';
import HazelcastClient from './HazelcastClient';
/**
 * Hearbeat Service
 */
export declare class Heartbeat {
    private client;
    private heartbeatTimeout;
    private heartbeatInterval;
    private listeners;
    private logger;
    private timer;
    constructor(client: HazelcastClient);
    /**
     * Starts sending periodic heartbeat operations.
     */
    start(): void;
    /**
     * Cancels scheduled heartbeat operations.
     */
    cancel(): void;
    /**
     * Registers a heartbeat listener. Listener is invoked when a heartbeat related event occurs.
     * @param heartbeatListener
     */
    addListener(heartbeatListener: ConnectionHeartbeatListener): void;
    private heartbeatFunction();
    private onHeartbeatStopped(connection);
    private onHeartbeatRestored(connection);
}
