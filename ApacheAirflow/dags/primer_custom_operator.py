from airflow import DAG
from datetime import datetime
from hello_operator import HelloOperator

with DAG(dag_id="custom_operator",
         description="primer custom operator",
         start_date=datetime(2022,8,1)
         ) as dag:

    t1 = HelloOperator(task_id="hello",
                        name="Freddy")

