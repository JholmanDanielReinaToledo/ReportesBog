import { FC } from "react";
import {
  Form,
  Input,
  Button,
  Radio,
  Select,
  Cascader,
  DatePicker,
  InputNumber,
  TreeSelect,
  Switch,
  Checkbox,
  Upload,
} from 'antd';
import { map } from "lodash";

type FiltrosInconvenientesProps = {
  setFiltros: (values: any) => void;
  filtros: any;
  localidades: string[];
}

const FiltrosInconvenientes:FC<FiltrosInconvenientesProps> = ({
 setFiltros,
 localidades,
 filtros,
})  => {

  return (
    <>
    <Button onClick={() => setFiltros(undefined)}>Limpiar filtros</Button>
        <Form
        initialValues={filtros}
      onValuesChange={(_, all) => {
        setFiltros(all)
      }}
    >
      <Form.Item label="Localidad" name="localidad">
        <Select>
        {
          map(localidades, (localidad) => {
            return (
              <Select.Option value={localidad}>{localidad}</Select.Option>
            )
          })
        }
        </Select>
        </Form.Item>
    </Form>
    </>
  )
};

export default FiltrosInconvenientes;