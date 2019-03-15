import { TopicOverloadPolicy } from '../proxy/topic/TopicOverloadPolicy';
export declare class ReliableTopicConfig {
    name: string;
    readBatchSize: number;
    overloadPolicy: TopicOverloadPolicy;
    toString(): string;
    clone(): ReliableTopicConfig;
}
