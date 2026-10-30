from datetime import datetime, timedelta
from airflow import DAG
from airflow.operators.bash import BashOperator

# 1. Define default settings for our job
default_args = {
    'owner': 'gemini_enterprise',
    'depends_on_past': False,
    'start_date': datetime(2026, 10, 1),
    'email_on_failure': False,
    'retries': 2,                             # ⚠️ If the jar crashes, Airflow automatically retries
    'retry_delay': timedelta(minutes=5),      # Wait 5 minutes before retrying
}

# 2. Instantiate the DAG (The workflow scheduler)
with DAG(
    'mysql_to_local_json_pipeline',
    default_args=default_args,
    description='Orchestrates local Spring Boot Java Connector Framework',
    schedule_interval='@daily',               # ⚠️ Runs once every day at midnight
    catchup=False,
) as dag:

    # 3. Define the bash task to execute our compiled Spring Boot JAR
    run_java_connector = BashOperator(
        task_id='execute_spring_boot_connector',
        # Executes your compiled Spring Boot application inside the container
        bash_command='java -jar /opt/airflow/apps/demo-0.0.1-SNAPSHOT.jar',
    )

    run_java_connector
