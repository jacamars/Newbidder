/// <reference types="bluebird" />
import * as Promise from 'bluebird';
import HazelcastClient from '../../HazelcastClient';
import { BaseProxy } from '../BaseProxy';
import { Ringbuffer } from '../Ringbuffer';
import { ITopic } from './ITopic';
import { ReliableTopicMessage } from './ReliableTopicMessage';
import { MessageListener } from './MessageListener';
export declare const RINGBUFFER_PREFIX = "_hz_rb_";
export declare const TOPIC_INITIAL_BACKOFF = 100;
export declare const TOPIC_MAX_BACKOFF = 2000;
export declare class ReliableTopicProxy<E> extends BaseProxy implements ITopic<E> {
    private ringbuffer;
    private readonly localAddress;
    private readonly batchSize;
    private readonly runners;
    private readonly serializationService;
    private readonly overloadPolicy;
    constructor(client: HazelcastClient, serviceName: string, name: string);
    setRingbuffer(): Promise<void>;
    addMessageListener(listener: MessageListener<E>): string;
    removeMessageListener(id: string): boolean;
    publish(message: E): Promise<void>;
    getRingbuffer(): Ringbuffer<ReliableTopicMessage>;
    destroy(): Promise<void>;
    private addOrDiscard(reliableTopicMessage);
    private addWithError(reliableTopicMessage);
    private addOrOverwrite(reliableTopicMessage);
    private addWithBackoff(reliableTopicMessage);
    private trySendMessage(message, delay, resolve);
}
