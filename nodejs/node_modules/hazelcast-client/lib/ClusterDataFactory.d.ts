import { IdentifiedDataSerializable, IdentifiedDataSerializableFactory } from './serialization/Serializable';
export declare class ClusterDataFactory implements IdentifiedDataSerializableFactory {
    create(type: number): IdentifiedDataSerializable;
}
