/// <reference types="long" />
import * as Long from 'long';
import { ListenerMessageCodec } from '../ListenerMessageCodec';
import { ClientConnection } from './ClientConnection';
export declare class ClientEventRegistration {
    readonly serverRegistrationId: string;
    readonly correlationId: Long;
    readonly subscriber: ClientConnection;
    readonly codec: ListenerMessageCodec;
    constructor(serverRegistrationId: string, correlationId: Long, subscriber: ClientConnection, codec: ListenerMessageCodec);
    toString(): string;
}
