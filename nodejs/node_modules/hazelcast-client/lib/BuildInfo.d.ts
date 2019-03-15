export declare class BuildInfo {
    static readonly UNKNOWN_VERSION_ID: number;
    private static readonly MAJOR_VERSION_MULTIPLIER;
    private static readonly MINOR_VERSION_MULTIPLIER;
    private static readonly PATTERN;
    static calculateServerVersionFromString(versionString: string): number;
    static calculateServerVersion(major: number, minor: number, patch: number): number;
    static getClientVersion(): string;
}
