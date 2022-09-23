const withAntdLess = require('next-plugin-antd-less');

module.exports = withAntdLess({
  reactStrictMode: true,
  lessVarsFilePath: './styles/globalLessVars.less',
  lessVarsFilePathAppendToEndOfContent: true,
  cssLoaderOptions: {},
  /* config options here */
  webpack(config) {
    return config;
  },
});