import { DataInput, DataOutput } from './serialization/Data';
import { IdentifiedDataSerializable } from './serialization/Serializable';
declare class Address implements IdentifiedDataSerializable {
    host: string;
    port: number;
    type: number;
    constructor(host?: string, port?: number);
    readData(input: DataInput): any;
    writeData(output: DataOutput): void;
    getFactoryId(): number;
    getClassId(): number;
    equals(other: Address): boolean;
    toString(): string;
}
export = Address;
