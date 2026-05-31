/**
 * @type {Cypress.PluginConfig}
 */
const registerCodeCoverageTasks = require('@cypress/code-coverage/task');
const registerMochawesomeReporter = require('cypress-mochawesome-reporter/plugin');

export default (on: Cypress.PluginEvents, config: Cypress.PluginConfigOptions) => {
  registerMochawesomeReporter(on);
  return registerCodeCoverageTasks(on, config);
};
