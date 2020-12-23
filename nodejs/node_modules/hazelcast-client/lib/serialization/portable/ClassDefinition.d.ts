export declare class ClassDefinition {
    private factoryId;
    private classId;
    private version;
    private fields;
    constructor(factoryId: number, classId: number, version: number);
    addFieldDefinition(definition: FieldDefinition): void;
    getFieldCount(): number;
    getFactoryId(): number;
    getClassId(): number;
    getVersion(): number;
    getFieldType(name: string): FieldType;
    hasField(name: string): boolean;
    getField(name: string): FieldDefinition;
    getFieldById(index: number): FieldDefinition;
    equals(o: ClassDefinition): boolean;
}
export declare class FieldDefinition {
    private readonly index;
    private readonly fieldName;
    private readonly type;
    private readonly factoryId;
    private readonly classId;
    private readonly version;
    constructor(index: number, fieldName: string, type: FieldType, version: number, factoryId?: number, classId?: number);
    getType(): FieldType;
    getName(): string;
    getIndex(): number;
    getClassId(): number;
    getFactoryId(): number;
    getVersion(): number;
}
export declare enum FieldType {
    PORTABLE = 0,
    BYTE = 1,
    BOOLEAN = 2,
    CHAR = 3,
    SHORT = 4,
    INT = 5,
    LONG = 6,
    FLOAT = 7,
    DOUBLE = 8,
    UTF = 9,
    PORTABLE_ARRAY = 10,
    BYTE_ARRAY = 11,
    BOOLEAN_ARRAY = 12,
    CHAR_ARRAY = 13,
    SHORT_ARRAY = 14,
    INT_ARRAY = 15,
    LONG_ARRAY = 16,
    FLOAT_ARRAY = 17,
    DOUBLE_ARRAY = 18,
    UTF_ARRAY = 19,
}
