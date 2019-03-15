import { SerializationService } from '../SerializationService';
import { DataInput } from '../Data';
import { ClassDefinition } from './ClassDefinition';
import { Portable, VersionedPortable } from '../Serializable';
export declare class PortableContext {
    private service;
    private version;
    private classDefContext;
    constructor(service: SerializationService, portableVersion: number);
    getVersion(): number;
    readClassDefinitionFromInput(input: DataInput, factoryId: number, classId: number, version: number): ClassDefinition;
    lookupOrRegisterClassDefinition(p: Portable): ClassDefinition;
    lookupClassDefinition(factoryId: number, classId: number, version: number): ClassDefinition;
    registerClassDefinition(classDefinition: ClassDefinition): ClassDefinition;
    getClassVersion(portable: VersionedPortable | Portable): number;
}
