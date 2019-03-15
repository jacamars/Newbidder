import { LogLevel } from './LoggingService';
import { ILogger } from './ILogger';
export declare class DefaultLogger implements ILogger {
    private readonly level;
    constructor(level: number);
    log(level: LogLevel, objectName: string, message: string, furtherInfo: any): void;
    error(objectName: string, message: string, furtherInfo?: any): void;
    warn(objectName: string, message: string, furtherInfo?: any): void;
    info(objectName: string, message: string, furtherInfo?: any): void;
    debug(objectName: string, message: string, furtherInfo?: any): void;
    trace(objectName: string, message: string, furtherInfo?: any): void;
}
