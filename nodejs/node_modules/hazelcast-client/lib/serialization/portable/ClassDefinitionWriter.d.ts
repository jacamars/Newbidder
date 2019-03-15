/// <reference types="long" />
import { PortableWriter } from './PortableSerializer';
import { ClassDefinition } from './ClassDefinition';
import { PortableContext } from './PortableContext';
import { Portable } from '../Serializable';
import * as Long from 'long';
import { ClassDefinitionBuilder } from './ClassDefinitionBuilder';
export declare class ClassDefinitionWriter implements PortableWriter {
    private context;
    private builder;
    constructor(context: PortableContext, builder: ClassDefinitionBuilder);
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
    registerAndGet(): ClassDefinition;
    private createNestedClassDef(portable, nestedBuilder);
}
