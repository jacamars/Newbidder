/// <reference types="long" />
import { DefaultPortableReader } from './DefaultPortableReader';
import { PortableSerializer } from './PortableSerializer';
import { DataInput } from '../Data';
import { ClassDefinition } from './ClassDefinition';
import { Portable } from '../Serializable';
import * as Long from 'long';
export declare class MorphingPortableReader extends DefaultPortableReader {
    constructor(portableSerializer: PortableSerializer, input: DataInput, classDefinition: ClassDefinition);
    readInt(fieldName: string): number;
    readLong(fieldName: string): Long;
    readDouble(fieldName: string): number;
    readFloat(fieldName: string): number;
    readShort(fieldName: string): number;
    readPortableArray(fieldName: string): Portable[];
    readUTFArray(fieldName: string): string[];
    readShortArray(fieldName: string): number[];
    readFloatArray(fieldName: string): number[];
    readDoubleArray(fieldName: string): number[];
    readLongArray(fieldName: string): Long[];
    readIntArray(fieldName: string): number[];
    readCharArray(fieldName: string): string[];
    readBooleanArray(fieldName: string): boolean[];
    readByteArray(fieldName: string): number[];
    readChar(fieldName: string): string;
    readByte(fieldName: string): number;
    readBoolean(fieldName: string): boolean;
    readUTF(fieldName: string): string;
    private validateCompatibleAndCall(fieldName, expectedType, superFunc);
    private createIncompatibleClassChangeError(fd, expectedType);
}
