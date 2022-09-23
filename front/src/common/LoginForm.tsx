import { SafetyCertificateOutlined, UserOutlined } from '@ant-design/icons';
import {
  Button, Col, Divider, Form, Image, Layout, Row,
} from 'antd';
import { useForm } from 'antd/lib/form/Form';
import { FC } from 'react';
import styles from '../../styles/Login.module.less';
import InputPropio from './Components/Input';
import { UsuarioLogin } from './types';

type LoginFormProps = {
  onSubmit: (values: Partial<UsuarioLogin>) => void;
};

const LoginForm: FC<LoginFormProps> = ({
  onSubmit,
}) => {
  const [login] = useForm();

  return (
    <Layout className={styles.container}>
      <h5 className={styles.titulo}>
        Reportes BOG
      </h5>
      <Form<Partial<UsuarioLogin>>
        form={login}
        onFinish={onSubmit}
      >
        <Form.Item noStyle>
          <InputPropio
            prefix={<UserOutlined />}
            formProps={{
              name: 'usuario',
            }}
            titulo={null}
            isRequired
            disableNormalize
            noAsterix
            placeholder="Usuario"
            className={styles.inputForm}
          />
        </Form.Item>
        <Form.Item noStyle>
          <InputPropio
            prefix={<SafetyCertificateOutlined />}
            formProps={{
              name: 'password',
            }}
            titulo={null}
            isRequired
            noAsterix
            placeholder="Contraseña"
            className={styles.inputForm}
            isPassword
          />
        </Form.Item>
        <Row>
          <Col
            span={12}
            offset={0}
            style={{
              marginTop: '0.8em',
            }}
          >
            <a
              href={`${process.env.URL_FRONT}/reset`}
            >
              ¿Olvidaste tu contraseña?
            </a>
          </Col>
          <Col span={4} offset={1}>
            <Form.Item noStyle>
              <Button
                className={styles.botonEnviar}
                type="primary"
                size="large"
                htmlType="submit"
              >
                Iniciar sesión
              </Button>
            </Form.Item>
          </Col>
        </Row>
      </Form>
    </Layout>
  );
};

export default LoginForm;
