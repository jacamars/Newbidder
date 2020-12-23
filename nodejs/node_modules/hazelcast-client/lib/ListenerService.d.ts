/// <reference types="bluebird" />
import * as Promise from 'bluebird';
import { ConnectionHeartbeatListener } from './core/ConnectionHeartbeatListener';
import HazelcastClient from './HazelcastClient';
import { ClientConnection } from './invocation/ClientConnection';
import { ClientEventRegistration } from './invocation/ClientEventRegistration';
import { ListenerMessageCodec } from './ListenerMessageCodec';
export declare class ListenerService implements ConnectionHeartbeatListener {
    private client;
    private internalEventEmitter;
    private logger;
    private isShutdown;
    private isSmartService;
    private activeRegistrations;
    private failedRegistrations;
    private userRegistrationKeyInformation;
    private connectionRefreshTask;
    private connectionRefreshTaskInterval;
    constructor(client: HazelcastClient);
    start(): void;
    onConnectionAdded(connection: ClientConnection): void;
    onConnectionRemoved(connection: ClientConnection): void;
    onHeartbeatRestored(connection: ClientConnection): void;
    reregisterListeners(): void;
    reregisterListenersOnConnection(connection: ClientConnection): void;
    removeRegistrationsOnConnection(connection: ClientConnection): void;
    invokeRegistrationFromRecord(userRegistrationKey: string, connection: ClientConnection): Promise<ClientEventRegistration>;
    registerListener(codec: ListenerMessageCodec, registerHandlerFunc: any): Promise<string>;
    deregisterListener(userRegistrationKey: string): Promise<boolean>;
    isSmart(): boolean;
    shutdown(): void;
    protected connectionRefreshHandler(): void;
    protected registerListenerInternal(codec: ListenerMessageCodec, listenerHandlerFunc: Function): Promise<string>;
    private trySyncConnectToAllConnections();
}
