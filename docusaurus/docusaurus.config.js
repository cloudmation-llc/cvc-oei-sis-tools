module.exports = {
  title: 'CVC-OEI SIS Tools',
  tagline: 'The tagline of my site',
  url: 'https://your-docusaurus-test-site.com',
  baseUrl: '/',
  favicon: 'img/favicon.ico',
  organizationName: 'facebook', // Usually your GitHub org/user name.
  projectName: 'docusaurus', // Usually your repo name.
  themeConfig: {
    disableDarkMode: true,
    navbar: {
      title: 'CVC-OEI SIS Tools',
      logo: {
        alt: 'CVC-OEI Logo',
        src: 'img/cvc-logo.png',
      },
      links: [
        {
          href: 'https://github.com/cloudmation-llc/cvc-oei-sis-tools',
          label: 'GitHub',
          position: 'right',
        },
      ],
    },
    footer: {
      style: 'dark',
      copyright: `Copyright © ${new Date().getFullYear()} California Community Colleges Chancellor's Office. Built with Docusaurus.`,
    },
  },
  presets: [
    [
      '@docusaurus/preset-classic',
      {
        docs: {
          sidebarPath: require.resolve('./sidebars.js')
        },
        theme: {
          customCss: require.resolve('./src/css/custom.css'),
        },
      },
    ],
  ],
};
