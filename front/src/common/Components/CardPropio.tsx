import { ExclamationCircleOutlined, EyeOutlined, PlusOutlined } from '@ant-design/icons';
import {
  Button, Card, Col, Modal, Row,
} from 'antd';
import { FC, ReactNode } from 'react';
import styles from 'styles/Card.module.less';

const { Meta } = Card;
const { confirm } = Modal;

const showDeleteConfirm = (onConfirm: () => void, title: string, textButton: string) => confirm({
  title,
  icon: <ExclamationCircleOutlined />,
  okText: textButton,
  okType: 'danger',
  cancelText: 'Cancelar',
  onOk() {
    if (onConfirm) onConfirm();
  },
});

type CardPropioProps = {
  titulo: string;
  className?: string;
  actions?: {
    primary: {
      text: string;
      icon?: ReactNode;
      permission: boolean;
      function: () => void;
    };
    secondary?: {
      text: string;
      icon: ReactNode;
      permission: boolean;
      function: () => void;
      useConfirm?: {
        title: string,
        textButton: string,
      };
    };
  };
  description?: ReactNode,
  extra?: ReactNode,
  isCrear?: boolean;
  desactivarPrimaryFunctionAlCrear?: boolean,
};

const CardPropio: FC<CardPropioProps> = ({
  titulo,
  actions,
  description,
  extra,
  isCrear,
  className,
  desactivarPrimaryFunctionAlCrear = false,
}) => {
  const {
    primary,
    secondary,
  } = actions || {};

  const {
    text: textPrimary,
    icon: iconPrimary,
    permission: permissionPrimary,
    function: functionPrimary,
  } = primary || {};

  const {
    text: textSecondary,
    permission: permissionSecondary,
    icon: iconSecondary,
    function: functionSecondary,
    useConfirm,
  } = secondary || {};
  return (
    <Card
      hoverable
      className={className || styles.card}
      title={!isCrear && titulo}
      extra={extra}
      onClick={
        isCrear && functionPrimary
          ? functionPrimary
          : undefined
      }
    >

      <Meta
        className={isCrear ? styles.metaCrear : styles.meta}
        description={
        isCrear ? (
          <PlusOutlined className={styles.plusIcon} />
        ) : (
          description
        )
        }
      />
      {actions && (
        <Row className={styles.actions}>
          {permissionSecondary && (
          <Col flex="1 1 30px">
            <Button
              icon={iconSecondary}
              onClick={() => {
                if (useConfirm && functionSecondary) {
                  showDeleteConfirm(functionSecondary, useConfirm.title, useConfirm.textButton);
                } else if (functionSecondary) {
                  functionSecondary();
                }
              }}
              className={styles.button}
              aria-label={textSecondary}
              role="button"
            >
              {textSecondary}
            </Button>
          </Col>
          )}
          {permissionPrimary && (
          <Col flex="1 1 30px">
            <Button
              className={styles.button}
              type="primary"
              icon={iconPrimary || <EyeOutlined />}
              onClick={desactivarPrimaryFunctionAlCrear ? undefined : functionPrimary}
              aria-label={textPrimary}
              role="button"
            >
              {textPrimary}
            </Button>
          </Col>
          )}
        </Row>
      )}
    </Card>
  );
};

export default CardPropio;
