module.exports = {
  moduleNameMapper: {
    '@core/(.*)': '<rootDir>/src/app/core/$1',
  },
  preset: 'jest-preset-angular',
  setupFilesAfterEnv: ['<rootDir>/setup-jest.ts'],
  bail: false,
  verbose: false,
  collectCoverage: false,
  collectCoverageFrom: [
    'src/app/**/*.ts',
    '!src/app/**/*.interface.ts',
    '!src/app/**/*.module.ts',
    '!src/app/app.config.ts',
    '!src/app/app.routes.ts',
    '!src/app/main.ts',
  ],
  coverageDirectory: './coverage/jest',
  coverageReporters: ['text', 'text-summary', 'lcov', 'html'],
  testPathIgnorePatterns: ['<rootDir>/node_modules/', '<rootDir>/cypress/'],
  coveragePathIgnorePatterns: ['<rootDir>/node_modules/', '<rootDir>/cypress/'],
  coverageThreshold: {
    global: {
      statements: 80,
      branches: 80,
      functions: 80,
      lines: 80,
    },
  },
  roots: [
    "<rootDir>"
  ],
  modulePaths: [
    "<rootDir>"
  ],
  moduleDirectories: [
    "node_modules"
  ],
};
