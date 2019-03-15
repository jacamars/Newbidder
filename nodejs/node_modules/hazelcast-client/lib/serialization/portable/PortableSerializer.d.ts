/// <reference types="long" />
import { SerializationService, Serializer } from '../SerializationService';
import { Portable } from '../Serializable';
import { DataInput, PositionalDataOutput } from '../Data';
import { FieldType } from './ClassDefinition';
import * as Long from 'long';
import { SerializationConfig } from '../../config/SerializationConfig';
export declare class PortableSerializer implements Serializer {
    private portableContext;
    private factories;
    private service;
    constructor(service: SerializationService, serializationConfig: SerializationConfig);
    getId(): number;
    read(input: DataInput): any;
    readObject(input: DataInput, factoryId: number, classId: number): Portable;
    write(output: PositionalDataOutput, object: Portable): void;
    writeObject(output: PositionalDataOutput, object: Portable): void;
    private createNewPortableInstance(factoryId, classId);
}
export interface PortableWriter {
    writeInt(fieldName: string, value: number): void;
    writeLong(fieldName: string, long: Long): void;
    writeUTF(fieldName: string, str: string): void;
    writeBoolean(fieldName: string, value: boolean): void;
    writeByte(fieldName: string, value: number): void;
    writeChar(fieldName: string, char: string): void;
    writeDouble(fieldName: string, double: number): void;
    writeFloat(fieldName: string, float: number): void;
    writeShort(fieldName: string, value: number): void;
    writePortable(fieldName: string, portable: Portable): void;
    writeNullPortable(fieldName: string, factoryId: number, classId: number): void;
    writeByteArray(fieldName: string, bytes: number[]): void;
    writeBooleanArray(fieldName: string, booleans: boolean[]): void;
    writeCharArray(fieldName: string, chars: string[]): void;
    writeIntArray(fieldName: string, ints: number[]): void;
    writeLongArray(fieldName: string, longs: Long[]): void;
    writeDoubleArray(fieldName: string, doubles: number[]): void;
    writeFloatArray(fieldName: string, floats: number[]): void;
    writeShortArray(fieldName: string, shorts: number[]): void;
    writeUTFArray(fieldName: string, val: string[]): void;
    writePortableArray(fieldName: string, portables: Portable[]): void;
}
export interface PortableReader {
    getVersion(): number;
    hasField(fieldName: string): boolean;
    getFieldNames(): string[];
    getFieldType(fieldName: string): FieldType;
    readInt(fieldName: string): number;
    readLong(fieldName: string): Long;
    readUTF(fieldName: string): string;
    readBoolean(fieldName: string): boolean;
    readByte(fieldName: string): number;
    readChar(fieldName: string): string;
    readDouble(fieldName: string): number;
    readFloat(fieldName: string): number;
    readShort(fieldName: string): number;
    readPortable(fieldName: string): Portable;
    readByteArray(fieldName: string): number[];
    readBooleanArray(fieldName: string): boolean[];
    readCharArray(fieldName: string): string[];
    readIntArray(fieldName: string): number[];
    readLongArray(fieldName: string): Long[];
    readDoubleArray(fieldName: string): number[];
    readFloatArray(fieldName: string): number[];
    readShortArray(fieldName: string): number[];
    readUTFArray(fieldName: string): string[];
    readPortableArray(fieldName: string): Portable[];
}
