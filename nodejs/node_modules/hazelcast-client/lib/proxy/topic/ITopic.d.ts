/// <reference types="bluebird" />
import * as Promise from 'bluebird';
import { DistributedObject } from '../../DistributedObject';
import { MessageListener } from './MessageListener';
export interface ITopic<E> extends DistributedObject {
    addMessageListener(listener: MessageListener<E>): string;
    removeMessageListener(id: string): boolean;
    publish(message: E): Promise<void>;
}
