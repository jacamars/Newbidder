import { Property } from '../config/Properties';
import { ILogger } from './ILogger';
export declare enum LogLevel {
    OFF = -1,
    ERROR = 0,
    WARN = 1,
    INFO = 2,
    DEBUG = 3,
    TRACE = 4,
}
export declare class LoggingService {
    private readonly logger;
    constructor(customLogger: ILogger, logLevel: number);
    isLogger(loggingProperty: Property): loggingProperty is ILogger;
    getLogger(): ILogger;
}
