import { SerializationService } from '../../serialization/SerializationService';
import { Ringbuffer } from '../Ringbuffer';
import { ReliableTopicMessage } from './ReliableTopicMessage';
import { ReliableTopicProxy } from './ReliableTopicProxy';
import { MessageListener } from './MessageListener';
import { ILogger } from '../../logging/ILogger';
export declare class ReliableTopicListenerRunner<E> {
    sequenceNumber: number;
    private listener;
    private ringbuffer;
    private batchSize;
    private serializationService;
    private cancelled;
    private logger;
    private proxy;
    private listenerId;
    constructor(listenerId: string, listener: MessageListener<E>, ringbuffer: Ringbuffer<ReliableTopicMessage>, batchSize: number, serializationService: SerializationService, logger: ILogger, proxy: ReliableTopicProxy<E>);
    next(): void;
    cancel(): void;
}
