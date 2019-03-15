/// <reference types="bluebird" />
import * as Promise from 'bluebird';
import HazelcastClient from '../HazelcastClient';
import { ClientConnection } from './ClientConnection';
import ClientMessage = require('../ClientMessage');
export declare class ConnectionAuthenticator {
    private connection;
    private client;
    private clusterService;
    private logger;
    constructor(connection: ClientConnection, client: HazelcastClient);
    authenticate(asOwner: boolean): Promise<void>;
    createCredentials(asOwner: boolean): ClientMessage;
}
