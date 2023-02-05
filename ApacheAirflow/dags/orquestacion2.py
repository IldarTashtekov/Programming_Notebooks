#usaremos sintaxis cron

from airflow import DAG
from airflow.operators.bash import BashOperator
from datetime import datetime

with DAG(dag_id="orquestracion2",
         description="probando orquestracion",
         start_date=datetime(2022,11,12),
         end_date=datetime(2022,12,30),
         #la sintaxis cron como podreis observar
         schedule_interval="0 7 * * 1") as dag:

    t1 = BashOperator(task_id = "tarea1",
                      bash_command= "sleep 2 && echo 'Tarea 1'")
    
    t2 = BashOperator(task_id = "tarea2",
                      bash_command= "sleep 2 && echo 'Tarea 2'")

    
    t3 = BashOperator(task_id = "tarea3",
                      bash_command= "sleep 2 && echo 'Tarea 3'")

    
    t4 = BashOperator(task_id = "tarea4",
                      bash_command= "sleep 2 && echo 'Tarea 4'")


    t1 >> t2 >> t3 >>t4