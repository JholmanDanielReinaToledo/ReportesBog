import { Spin } from 'antd'
import Layout from 'antd/lib/layout/layout'
import type { NextPage } from 'next'
import { useEffect, useState } from 'react';
import Head from 'next/head'
import styles from '../styles/Home.module.less';
import LoginForm from '../src/common/LoginForm';
import { UsuarioLogin, UsuarioLoginToken } from '../src/common/types';
import { login } from '../src/functions/users';
import { size } from 'lodash';
import { useRouter } from 'next/router';

const Home: NextPage = () => {
  const [token, setToken] = useState();
  const [isLoading, setIsLoading] = useState(false);
  const { push } = useRouter();

  const loginS = async (values: UsuarioLogin) => {
    const loginValues: UsuarioLoginToken = await login(values);
    console.log(loginValues);
    if (loginValues?.id) {
      window.localStorage.setItem(
        'currentUser',
        JSON.stringify(loginValues),
      );
      push('/reportes');
    }
  };

  useEffect(() => {
    const newUserString = window.localStorage.getItem('currentUser');
    if (size(newUserString) > 0) {
      const newUser = JSON.parse(newUserString || '');
      if (newUser?.id) {
        push('/reportes');
      }
    }
  }, []);

  return (
    <Layout className={styles.container}>
    <Head>
      <title>REPORTES BOG</title>
    </Head>

    <Layout className={styles.contenedor}>
      <Layout
        className={`${styles.layout} ${styles.layout1}`}
      />
      <Layout
        className={`${styles.layout} ${styles.layout2}`}
      />
    </Layout>
    <Layout className={styles.contenedor}>
      <Layout
        className={`${styles.layout} ${styles.layout3}`}
      />
      <Layout
        className={`${styles.layout} ${styles.layout4}`}
      />
    </Layout>
    <div className={styles.loginContainer}>
      {
        (isLoading && token)
          ? (
            <Spin />
          )
          : (
            <LoginForm
                onSubmit={loginS}
              />
          )
      }

    </div>
  </Layout>
  )
}

export default Home
