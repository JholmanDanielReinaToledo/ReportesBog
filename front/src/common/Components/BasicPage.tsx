import { Layout, Menu } from "antd";
import React, { useEffect } from "react";
import { useRouter } from "next/router";
import { size } from "lodash";
const { Header, Content, Footer, Sider } = Layout;

// @ts-ignore
const BasicPage = ({children}) => {
  const { push } = useRouter();

  useEffect(() => {
    const newUserString = window.localStorage.getItem('currentUser');
    if (size(newUserString) > 0) {
      const newUser = JSON.parse(newUserString || '');
      if (newUser?.id) {
        push('/reportes');
      }
    } else {
      push('/');
    }
  }, []);

  return (
    <Layout>
    <Sider
      breakpoint="lg"
      collapsedWidth="0"
      onBreakpoint={broken => {
        console.log(broken);
      }}
      onCollapse={(collapsed, type) => {
        console.log(collapsed, type);
      }}
    >
      <div className="logo" />
      <Menu
        theme="dark"
        mode="inline"
        defaultSelectedKeys={['4']}
      >
        <Menu.SubMenu title="Administración">
          <Menu.Item onClick={() => push('/administracion/usuarios')}>Usuarios</Menu.Item>
          <Menu.Item onClick={() => push('/administracion/usuarios')}>Grupos y permisos</Menu.Item>
          <Menu.Item onClick={() => push('/administracion/dominios')}>Dominios</Menu.Item>
        </Menu.SubMenu>
      </Menu>
    </Sider>
    <Layout>
      <Header className="site-layout-sub-header-background" style={{ padding: 0 }} />
      <Content style={{ margin: '24px 16px 0' }}>
        <div className="site-layout-background" style={{ padding: 24, minHeight: 360 }}>
          {children}
        </div>
      </Content>
      <Footer style={{ textAlign: 'center' }}>Ant Design ©2018 Created by Ant UED</Footer>
    </Layout>
  </Layout>
  );
};

export default BasicPage;