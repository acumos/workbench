import { extend, configure } from "vee-validate";
import * as rules from "vee-validate/dist/rules";
import en from "vee-validate/dist/locale/en";

for (let rule in rules) {
  extend(rule, {
    ...rules[rule],
    message: en.messages[rule]
  });
}

// Configuration
configure({
  classes: {
    invalid: "border-red-500"
  }
});
