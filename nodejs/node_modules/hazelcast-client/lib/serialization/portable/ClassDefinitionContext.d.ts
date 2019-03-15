import { ClassDefinition } from './ClassDefinition';
export declare class ClassDefinitionContext {
    private factoryId;
    private classDefs;
    constructor(factoryId: number);
    private static encodeVersionedClassId(classId, version);
    lookup(classId: number, version: number): ClassDefinition;
    register(classDefinition: ClassDefinition): ClassDefinition;
}
