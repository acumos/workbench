export default {
  validate: value => /^[a-zA-Z][a-zA-Z0-9_]{5,29}$/.test(value),
  message: '{_field_} should contain only 6-30 alphanumeric characters, may include "_" and should not begin with number'
};
