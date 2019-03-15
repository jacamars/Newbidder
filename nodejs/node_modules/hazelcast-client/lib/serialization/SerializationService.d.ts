import { SerializationConfig } from '../config/SerializationConfig';
import { Data, DataInput, DataOutput } from './Data';
import HazelcastClient from '../HazelcastClient';
export interface SerializationService {
    toData(object: any, paritioningStrategy?: any): Data;
    toObject(data: Data): any;
    writeObject(out: DataOutput, object: any): void;
    readObject(inp: DataInput): any;
}
export interface Serializer {
    getId(): number;
    read(input: DataInput): any;
    write(output: DataOutput, object: any): void;
}
export declare class SerializationServiceV1 implements SerializationService {
    private registry;
    private serializerNameToId;
    private numberType;
    private serializationConfig;
    private client;
    constructor(client: HazelcastClient, serializationConfig: SerializationConfig);
    isData(object: any): boolean;
    toData(object: any, partitioningStrategy?: any): Data;
    toObject(data: Data): any;
    writeObject(out: DataOutput, object: any): void;
    readObject(inp: DataInput): any;
    registerSerializer(name: string, serializer: Serializer): void;
    /**
     * Serialization precedence
     *  1. NULL
     *  2. DataSerializable
     *  3. Portable
     *  4. Default Types
     *      * Byte, Boolean, Character, Short, Integer, Long, Float, Double, String
     *      * Array of [Byte, Boolean, Character, Short, Integer, Long, Float, Double, String]
     *      * Java types [Date, BigInteger, BigDecimal, Class, Enum]
     *  5. Custom serializers
     *  6. Global Serializer
     *  7. Fallback (JSON)
     * @param obj
     * @returns
     */
    findSerializerFor(obj: any): Serializer;
    protected lookupDefaultSerializer(obj: any): Serializer;
    protected lookupCustomSerializer(obj: any): Serializer;
    protected lookupGlobalSerializer(): Serializer;
    protected isIdentifiedDataSerializable(obj: any): boolean;
    protected isPortableSerializable(obj: any): boolean;
    protected registerDefaultSerializers(): void;
    protected registerIdentifiedFactories(): void;
    protected registerCustomSerializers(): void;
    protected registerGlobalSerializer(): void;
    protected assertValidCustomSerializer(candidate: any): void;
    protected isCustomSerializable(object: any): boolean;
    protected findSerializerByName(name: string, isArray: boolean): Serializer;
    protected findSerializerById(id: number): Serializer;
    protected calculatePartitionHash(object: any, strategy: Function): number;
    private defaultPartitionStrategy(obj);
}
