/// <reference types="node" />
/// <reference types="bluebird" />
import * as Promise from 'bluebird';
import { EventEmitter } from 'events';
import HazelcastClient from '../HazelcastClient';
import { ClientConnection } from './ClientConnection';
import { AddressTranslator } from '../connection/AddressTranslator';
import { AddressProvider } from '../connection/AddressProvider';
import Address = require('../Address');
/**
 * Maintains connections between the client and members of the cluster.
 */
export declare class ClientConnectionManager extends EventEmitter {
    establishedConnections: {
        [address: string]: ClientConnection;
    };
    readonly addressProviders: AddressProvider[];
    private readonly client;
    private pendingConnections;
    private logger;
    private readonly addressTranslator;
    constructor(client: HazelcastClient, addressTranslator: AddressTranslator, addressProviders: AddressProvider[]);
    getActiveConnections(): {
        [address: string]: ClientConnection;
    };
    /**
     * Returns the {@link ClientConnection} with given {@link Address}. If there is no such connection established,
     * it first connects to the address and then return the {@link ClientConnection}.
     * @param address
     * @param asOwner Sets the connected node as owner of this client if true.
     * @returns {Promise<ClientConnection>|Promise<T>}
     */
    getOrConnect(address: Address, asOwner?: boolean): Promise<ClientConnection>;
    /**
     * Destroys the connection with given node address.
     * @param address
     */
    destroyConnection(address: Address): void;
    shutdown(): void;
    private triggerConnect(address, asOwner);
    private connectTLSSocket(address, configOpts);
    private connectNetSocket(address);
    private initiateCommunication(connection);
    private onConnectionClosed(connection);
    private onConnectionOpened(connection);
    private authenticate(connection, ownerConnection);
}
