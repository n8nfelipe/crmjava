module.exports = function (config) {
  config.set({
    frameworks: ["jasmine"],
    browsers: ["ChromeHeadlessNoSandbox"],
    customLaunchers: {
      ChromeHeadlessNoSandbox: {
        base: "ChromeHeadless",
        flags: ["--no-sandbox", "--disable-gpu"],
      },
    },
    reporters: ["progress"],
    singleRun: true,
  });
};
