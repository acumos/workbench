export default {
  validate: value => /^[a-zA-Z0-9_.]{1,14}$/.test(value),
  message: '{_field_} should contain only 1-14 alphanumeric characters, may include "_" and "."'
};
