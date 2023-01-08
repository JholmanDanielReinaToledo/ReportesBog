import { Layout, Menu } from "antd";
import React, { useEffect } from "react";
import { useRouter } from "next/router";
import { size } from "lodash";
const { Header, Content, Footer, Sider } = Layout;

// @ts-ignore
const BasicPage = ({children}) => {
  const { push, asPath } = useRouter();

  useEffect(() => {
    console.log(window)
    if (typeof window !== "undefined") {
      // Client-side-only code
      const newUserString = window?.localStorage?.getItem('currentUser');
      if (size(newUserString) > 0) {
        const newUser = JSON.parse(newUserString || '');
        if (newUser?.id && asPath == '/') {
          push('/reportes');
        }
      } else {
        push('/');
      }
    }
  }, []);

  return (
    <Layout style={{
      height: '100vh',
    }}>
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
        <Menu.Item onClick={() => push('/reportes')}>Reportes</Menu.Item>
      </Menu>
    </Sider>
    <Layout>
      <Content style={{ margin: '24px 16px 0' }}>
        <div className="site-layout-background" style={{ padding: 24, minHeight: 360 }}>
          {children}
        </div>
      </Content>
      <Footer style={{ textAlign: 'center' }}>Reportes BOG ©2022</Footer>
    </Layout>
  </Layout>
  );
};

export default BasicPage;