/**
 * @type {Cypress.PluginConfig}
 */
const registerCodeCoverageTasks = require('@cypress/code-coverage/task');

export default (on: Cypress.PluginEvents, config: Cypress.PluginConfigOptions) => {
  return registerCodeCoverageTasks(on, config);
};
