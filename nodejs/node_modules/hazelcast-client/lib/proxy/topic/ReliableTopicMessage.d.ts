/// <reference types="long" />
import * as Long from 'long';
import { Data, DataInput, DataOutput } from '../../serialization/Data';
import { IdentifiedDataSerializable, IdentifiedDataSerializableFactory } from '../../serialization/Serializable';
import Address = require('../../Address');
export declare const RELIABLE_TOPIC_MESSAGE_FACTORY_ID = -18;
export declare const RELIABLE_TOPIC_CLASS_ID = 2;
export declare class ReliableTopicMessage implements IdentifiedDataSerializable {
    publishTime: Long;
    publisherAddress: Address;
    payload: Data;
    readData(input: DataInput): any;
    writeData(output: DataOutput): void;
    getFactoryId(): number;
    getClassId(): number;
}
export declare class ReliableTopicMessageFactory implements IdentifiedDataSerializableFactory {
    create(type: number): IdentifiedDataSerializable;
}
