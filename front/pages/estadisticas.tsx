import { useEffect, useState } from "react";
import BasicPage from "../src/common/Components/BasicPage";
import { Inconveniente } from "../src/types";
import { useQuery } from "@apollo/client";
import { GET_ALL_INCONVENIENTES } from "../src/graphql/querys";
import { groupBy, map, orderBy, size } from "lodash";
import { PieChart, Pie, Legend, Tooltip, Cell } from "recharts";
import { Space } from "antd";

const COLORS = [
  "#0088FE",
  "#00C49F",
  "#FFBB28",
  "#FF8042",
  "#ff3366",
  "#ff66cc",
  "#cc33ff",
  "#9933ff",
  "#6666ff",
  "#33ccff",
  "#00ffcc",
  "#99ff33",
  "#ffff00",
  "#ff6600",
  "#cc0000",
];
const EstadisticaPage = () => {
  const [inconvenientes, setInconvenientes] = useState<Inconveniente[]>([]);
  const [inconvenientesConDir, setInconvenientesConDir] = useState<
    Inconveniente[]
  >([]);
  const [conteo, setConteo] = useState({
    con: 0,
    sin: 0,
  });
  const [localidades, setLocalidades] = useState([]);
  const [estadosReportes, setEstadosReportes] = useState([]);

  const { loading, error, data } = useQuery(GET_ALL_INCONVENIENTES);

  useEffect(() => {
    if (data) {
      setInconvenientes(data.allInconvenientes.nodes);
    }
  }, [data]);

  useEffect(() => {
    if (size(inconvenientes) > 0) {
      const conteo = {
        con: 0,
        sin: 0,
      };

      // @ts-ignore
      const reportesConDirTemp: Inconveniente[] = map(
        inconvenientes,
        (inconveniente) => {
          if (inconveniente?.direccionByIdDireccion) {
            conteo.con = conteo.con + 1;
            return inconveniente;
          } else {
            conteo.sin = conteo.sin + 1;
          }
        }
      ).filter(Boolean);

      const inconvenientesPorLocal = {};

      map(reportesConDirTemp, (inconveniente) => {
        const localidad =
          inconveniente?.direccionByIdDireccion?.localidad?.descripcion;
        // @ts-ignore
        if (inconvenientesPorLocal[localidad]) {
          // @ts-ignore
          inconvenientesPorLocal[localidad] += 1;
        } else {
          // @ts-ignore
          inconvenientesPorLocal[localidad] = 1;
        }
      });

      const outputArray = Object.keys(inconvenientesPorLocal).map((key) => ({
        name: key,
        // @ts-ignore
        value: inconvenientesPorLocal[key],
      }));

      const estadosInconvenientes = {
        
      };

      map(inconvenientes, (inconveniente) => {
        const estado = inconveniente?.estadoReporteByIdEstado?.descripcion;
        // @ts-ignore
        if (estadosInconvenientes[estado]) {
        // @ts-ignore
          estadosInconvenientes[estado] += 1;
        } else {
        // @ts-ignore
          estadosInconvenientes[estado] = 1;
        }
      });

      console.log(estadosInconvenientes);

      const outputArray2 = Object.keys(estadosInconvenientes).map((key) => ({
        name: key,
        // @ts-ignore
        value: estadosInconvenientes[key],
      }));
      // @ts-ignore
      setEstadosReportes(outputArray2);
      // @ts-ignore
      setLocalidades(outputArray);
      setInconvenientesConDir(reportesConDirTemp);
      setConteo(conteo);
    }
  }, [inconvenientes]);

  const data1 = [
    { name: "Sin dirección", value: conteo.sin },
    { name: "Con dirección", value: conteo.con },
  ];
  console.log(estadosReportes);
  return (
    <BasicPage>
      <Space wrap>
        <PieChart width={400} height={400}>
          <Pie
            data={data1}
            cx={200}
            cy={200}
            innerRadius={60}
            outerRadius={80}
            fill='#8884d8'
            dataKey='value'>
            {data1?.map((_, index) => (
              <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
            ))}
          </Pie>
          <Tooltip />
          <Legend />
        </PieChart>
        <PieChart width={400} height={400}>
          <Legend />
          <Pie
            data={localidades}
            cx='50%'
            cy='50%'
            outerRadius={80}
            label
            fill='#8884d8'
            dataKey='value'>
            {localidades?.map((_, index) => (
              <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
            ))}
          </Pie>
        </PieChart>

        <PieChart width={400} height={400}>
          <Legend />
          <Pie
            data={estadosReportes}
            cx='50%'
            cy='50%'
            outerRadius={80}
            label
            fill='#8884d8'
            dataKey='value'>
            {localidades?.map((_, index) => (
              <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
            ))}
          </Pie>
        </PieChart>

      </Space>
    </BasicPage>
  );
};

export default EstadisticaPage;
