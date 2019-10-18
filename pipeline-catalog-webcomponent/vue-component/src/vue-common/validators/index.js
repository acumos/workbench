import { extend, configure } from "vee-validate";
import * as rules from "vee-validate/dist/rules";
import en from "vee-validate/dist/locale/en";

// Custom Rules
import startAlpha from "./startAlpha";
import versionValidation from "./versionValidation";
import url from "./url";

for (let rule in rules) {
  extend(rule, {
    ...rules[rule],
    message: en.messages[rule]
  });
}

// Add Custom Rules
extend("startAlpha", startAlpha);
extend("url", url);
extend("versionValidation", versionValidation);

// Configuration
configure({
  classes: {
    invalid: "border-red-500"
  }
});
