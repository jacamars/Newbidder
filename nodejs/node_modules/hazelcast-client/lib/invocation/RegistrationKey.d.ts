import ClientMessage = require('../ClientMessage');
import { ListenerMessageCodec } from '../ListenerMessageCodec';
export declare class RegistrationKey {
    private readonly userRegistrationId;
    private registerHandlerFunc;
    private registerRequest;
    private codec;
    constructor(regId: string, codec: ListenerMessageCodec, registerRequest?: ClientMessage, registerHandlerFunc?: Function);
    getRegisterRequest(): ClientMessage;
    setRegisterRequest(registerRequest: ClientMessage): void;
    getCodec(): ListenerMessageCodec;
    setCodec(value: ListenerMessageCodec): void;
    getHandler(): Function;
    setHandler(handler: Function): void;
    getUserRegistrationKey(): string;
}
