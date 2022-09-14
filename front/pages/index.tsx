import { Button, Spin } from 'antd'

import Layout from 'antd/lib/layout/layout'
import type { NextPage } from 'next'
import { useEffect, useState } from 'react';
import Head from 'next/head'
import Image from 'next/image'
import SpinFC from 'antd/lib/spin';
import styles from '../styles/Home.module.less';

const Home: NextPage = () => {
  const [token, setToken] = useState();
  const [isLoading, setIsLoading] = useState(false);

  const login = () => {

  }

  return (
    <Layout className={styles.container}>
    <Head>
      <title>SISDEP</title>
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
            <>{
              /**
               * <LoginForm
                onSubmit={loguear}
              />
               */
            }
              Formulario
            </>
          )
      }

    </div>
  </Layout>
  )
}

export default Home
