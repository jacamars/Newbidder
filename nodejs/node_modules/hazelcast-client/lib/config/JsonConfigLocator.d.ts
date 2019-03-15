/// <reference types="bluebird" />
/// <reference types="node" />
import * as Promise from 'bluebird';
export declare class JsonConfigLocator {
    static readonly ENV_VARIABLE_NAME: string;
    static readonly DEFAULT_FILE_NAME: string;
    private buffer;
    private configLocation;
    private logger;
    load(): Promise<void>;
    loadFromEnvironment(): Promise<boolean>;
    loadFromWorkingDirectory(): Promise<boolean>;
    loadImported(path: string): Promise<Buffer>;
    loadPath(path: string): Promise<Buffer>;
    getBuffer(): Buffer;
}
