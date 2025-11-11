#!/usr/bin/env python3
"""
Generate port interface diagrams for the Samstraumr project.

This script generates detailed diagrams for the port interfaces and their
relationships in the Samstraumr Clean Architecture implementation.

Example usage:
    ./bin/port_interfaces_diagram.py --output svg
    ./bin/port_interfaces_diagram.py --detail high

Dependencies:
    - diagrams (pip install diagrams)
    - graphviz (system package)
"""

import os
import sys
import argparse
from pathlib import Path

# Import diagrams if available
try:
    from diagrams import Diagram, Cluster, Edge
    from diagrams.programming.language import Java
    from diagrams.onprem.database import PostgreSQL
    from diagrams.onprem.client import Users, User
    from diagrams.onprem.compute import Server
    from diagrams.generic.storage import Storage
    from diagrams.programming.framework import Spring
    from diagrams.generic.os import Windows, LinuxGeneral
    from diagrams.onprem.network import Nginx
    from diagrams.onprem.queue import Kafka
    from diagrams.onprem.monitoring import Grafana
    diagrams_available = True
except ImportError:
    diagrams_available = False
    print("Warning: diagrams package not available. Please install it with:")
    print("  pip install diagrams")
    print("And make sure graphviz is installed:")
    print("  apt-get install graphviz  # Ubuntu/Debian")
    print("  brew install graphviz      # macOS")


class PortInterfaceDiagramGenerator:
    """Generate diagrams for port interfaces in the Samstraumr Clean Architecture."""
    
    def __init__(self, output_dir: str = "docs/diagrams", output_format: str = "svg",
                 detail_level: str = "medium"):
        """Initialize the diagram generator.
        
        Args:
            output_dir: Directory to save output files
            output_format: Output format (png, svg, pdf)
            detail_level: Detail level for diagrams (low, medium, high)
        """
        self.output_format = output_format.lower()
        if self.output_format not in ("png", "svg", "pdf"):
            print(f"Unsupported output format: {output_format}, defaulting to svg")
            self.output_format = "svg"
        
        self.detail_level = detail_level.lower()
        if self.detail_level not in ("low", "medium", "high"):
            print(f"Unsupported detail level: {detail_level}, defaulting to medium")
            self.detail_level = "medium"
        
        # Get output directory
        self.output_dir = Path(output_dir)
        
        # Create output directory if it doesn't exist
        self.output_dir.mkdir(parents=True, exist_ok=True)
    
    def generate_ports_component_diagram(self) -> str:
        """Generate a Clean Architecture component diagram with port interfaces.
        
        Returns:
            Path to the generated diagram file
        """
        if not diagrams_available:
            print("Diagrams library not available. Cannot generate component diagram.")
            return ""
        
        output_file = self.output_dir / f"samstraumr_ports_component_diagram.{self.output_format}"
        
        try:
            with Diagram(
                "Samstraumr Port Interfaces Component Diagram",
                filename=str(output_file.with_suffix("")),
                outformat=self.output_format,
                show=False,
                direction="TB",
            ):
                with Cluster("Domain Layer"):
                    component = Java("Component")
                    identity = Java("Identity")
                    state = Java("State")
                    machine = Java("Machine")
                    composite = Java("Composite")
                    event = Java("DomainEvent")
                
                with Cluster("Application Layer"):
                    with Cluster("Services"):
                        component_svc = Java("ComponentService")
                        notification_svc = Java("NotificationService")
                        cache_svc = Java("CacheService")
                        file_svc = Java("FileSystemService")
                        validation_svc = Java("ValidationService")
                        persistence_svc = Java("PersistenceService")
                        event_svc = Java("EventService")
                        security_svc = Java("SecurityService")
                        messaging_svc = Java("MessagingService")
                        task_svc = Java("TaskExecutionService")
                        config_svc = Java("ConfigurationService")
                    
                    with Cluster("Port Interfaces"):
                        notification_port = Java("NotificationPort")
                        cache_port = Java("CachePort")
                        file_port = Java("FileSystemPort")
                        validation_port = Java("ValidationPort")
                        persistence_port = Java("PersistencePort")
                        event_pub_port = Java("EventPublisherPort")
                        dataflow_port = Java("DataFlowEventPort")
                        security_port = Java("SecurityPort")
                        messaging_port = Java("MessagingPort")
                        task_port = Java("TaskExecutionPort")
                        config_port = Java("ConfigurationPort")
                        template_port = Java("TemplatePort")
                        storage_port = Java("StoragePort")
                        comp_repo_port = Java("ComponentRepository")
                
                with Cluster("Infrastructure Layer"):
                    with Cluster("Adapters"):
                        notification_adapter = Java("NotificationAdapter")
                        cache_adapter = Java("InMemoryCacheAdapter")
                        file_adapter = Java("StandardFileSystemAdapter")
                        validation_adapter = Java("ValidationAdapter")
                        persistence_adapter = Java("InMemoryPersistenceAdapter")
                        event_adapter = Java("EventPublisherAdapter")
                        event_dispatcher = Java("InMemoryEventDispatcher")
                        security_adapter = Java("SecurityAdapter")
                        messaging_adapter = Java("InMemoryMessagingAdapter")
                        task_adapter = Java("ThreadPoolTaskExecutionAdapter")
                        config_adapter = Java("ConfigurationAdapter")
                        template_adapter = Java("FreemarkerTemplateAdapter")
                        storage_adapter = Java("InMemoryStorageAdapter")
                        comp_repo_adapter = Java("InMemoryComponentRepository")
                
                with Cluster("External Systems"):
                    with Cluster("External Resources"):
                        database = PostgreSQL("Database")
                        file_system = Storage("File System")
                        email_server = Server("Email Server")
                        sms_gateway = Server("SMS Gateway")
                        message_bus = Kafka("Message Bus")
                        monitoring = Grafana("Monitoring")
                
                # Domain layer relationships
                component >> identity
                component >> state
                machine >> component
                composite >> component
                
                # Application layer - Service to Domain
                component_svc >> component
                component_svc >> machine
                component_svc >> composite
                
                # Application layer - Service to Ports
                notification_svc >> notification_port
                cache_svc >> cache_port
                file_svc >> file_port
                validation_svc >> validation_port
                persistence_svc >> persistence_port
                event_svc >> event_pub_port
                event_svc >> dataflow_port
                security_svc >> security_port
                messaging_svc >> messaging_port
                task_svc >> task_port
                config_svc >> config_port
                
                # Infrastructure - Adapters to Ports
                notification_port << notification_adapter
                cache_port << cache_adapter
                file_port << file_adapter
                validation_port << validation_adapter
                persistence_port << persistence_adapter
                event_pub_port << event_adapter
                dataflow_port << event_dispatcher
                security_port << security_adapter
                messaging_port << messaging_adapter
                task_port << task_adapter
                config_port << config_adapter
                template_port << template_adapter
                storage_port << storage_adapter
                comp_repo_port << comp_repo_adapter
                
                # External connections
                notification_adapter >> email_server
                notification_adapter >> sms_gateway
                file_adapter >> file_system
                persistence_adapter >> database
                event_adapter >> message_bus
                event_dispatcher >> message_bus
                
            print(f"Generated port interfaces component diagram: {output_file}")
            return str(output_file)
        
        except Exception as e:
            print(f"Failed to generate port interfaces component diagram: {e}")
            return ""
    
    def generate_ports_integration_diagram(self) -> str:
        """Generate a diagram showing the integration patterns between ports.
        
        Returns:
            Path to the generated diagram file
        """
        if not diagrams_available:
            print("Diagrams library not available. Cannot generate port integration diagram.")
            return ""
        
        output_file = self.output_dir / f"samstraumr_ports_integration_diagram.{self.output_format}"
        
        try:
            with Diagram(
                "Samstraumr Port Integration Patterns",
                filename=str(output_file.with_suffix("")),
                outformat=self.output_format,
                show=False,
                direction="TB",
            ):
                # Create port interfaces
                notification_port = Java("NotificationPort")
                cache_port = Java("CachePort")
                file_port = Java("FileSystemPort")
                validation_port = Java("ValidationPort")
                persistence_port = Java("PersistencePort")
                event_pub_port = Java("EventPublisherPort")
                security_port = Java("SecurityPort")
                
                # Create integration services
                with Cluster("Integration Services"):
                    cache_file_svc = Java("CachingFileService")
                    event_notif_svc = Java("EventNotificationService")
                    validation_persist_svc = Java("ValidationPersistenceService")
                    security_file_svc = Java("SecureFileService")
                
                # Show integration patterns
                
                # Cache-FileSystem integration
                cache_port >> cache_file_svc
                file_port >> cache_file_svc
                
                # Event-Notification integration
                event_pub_port >> event_notif_svc
                notification_port << event_notif_svc
                
                # Validation-Persistence integration
                validation_port >> validation_persist_svc
                persistence_port << validation_persist_svc
                
                # Security-FileSystem integration
                security_port >> security_file_svc
                file_port << security_file_svc
                
            print(f"Generated port integration diagram: {output_file}")
            return str(output_file)
        
        except Exception as e:
            print(f"Failed to generate port integration diagram: {e}")
            return ""
    
    def generate_detailed_port_diagram(self) -> str:
        """Generate a detailed diagram for each port interface.
        
        Returns:
            Path to the generated diagram file
        """
        if not diagrams_available:
            print("Diagrams library not available. Cannot generate detailed port diagram.")
            return ""
        
        output_file = self.output_dir / f"samstraumr_detailed_ports_diagram.{self.output_format}"
        
        try:
            with Diagram(
                "Samstraumr Detailed Port Interfaces",
                filename=str(output_file.with_suffix("")),
                outformat=self.output_format,
                show=False,
                direction="TB",
            ):
                # Focus on ports with their methods
                with Cluster("Core Port Interfaces"):
                    with Cluster("NotificationPort"):
                        notification = Java("NotificationPort")
                        with Cluster("Methods", direction="LR"):
                            Java("send()")
                            Java("sendBatch()")
                            Java("sendAsync()")
                            Java("register()")
                            Java("checkStatus()")
                    
                    with Cluster("CachePort"):
                        cache = Java("CachePort")
                        with Cluster("Methods", direction="LR"):
                            Java("get()")
                            Java("put()")
                            Java("remove()")
                            Java("clear()")
                            Java("contains()")
                    
                    with Cluster("FileSystemPort"):
                        file_system = Java("FileSystemPort")
                        with Cluster("Methods", direction="LR"):
                            Java("readFile()")
                            Java("writeFile()")
                            Java("deleteFile()")
                            Java("listFiles()")
                            Java("fileExists()")
                    
                    with Cluster("ValidationPort"):
                        validation = Java("ValidationPort")
                        with Cluster("Methods", direction="LR"):
                            Java("validate()")
                            Java("validateAll()")
                            Java("getViolations()")
                            Java("isValid()")
                    
                    with Cluster("PersistencePort"):
                        persistence = Java("PersistencePort")
                        with Cluster("Methods", direction="LR"):
                            Java("save()")
                            Java("find()")
                            Java("delete()")
                            Java("query()")
                            Java("transaction()")
                
                # Connect with implementation relationships if high detail level
                if self.detail_level == "high":
                    with Cluster("Standard Implementations"):
                        notif_impl = Java("NotificationAdapter")
                        cache_impl = Java("InMemoryCacheAdapter")
                        file_impl = Java("StandardFileSystemAdapter")
                        validation_impl = Java("ValidationAdapter")
                        persistence_impl = Java("InMemoryPersistenceAdapter")
                    
                    # Connect implementations to interfaces
                    notification << notif_impl
                    cache << cache_impl
                    file_system << file_impl
                    validation << validation_impl
                    persistence << persistence_impl
            
            print(f"Generated detailed port diagram: {output_file}")
            return str(output_file)
        
        except Exception as e:
            print(f"Failed to generate detailed port diagram: {e}")
            return ""
    
    def generate_clean_architecture_ports_diagram(self) -> str:
        """Generate a diagram showing ports in the Clean Architecture context.
        
        Returns:
            Path to the generated diagram file
        """
        if not diagrams_available:
            print("Diagrams library not available. Cannot generate Clean Architecture ports diagram.")
            return ""
        
        output_file = self.output_dir / f"samstraumr_clean_arch_ports_diagram.{self.output_format}"
        
        try:
            with Diagram(
                "Samstraumr Clean Architecture with Ports and Adapters",
                filename=str(output_file.with_suffix("")),
                outformat=self.output_format,
                show=False,
                direction="TB",
            ):
                with Cluster("Core Domain Layer"):
                    entities = Java("Domain Entities")
                    value_objects = Java("Value Objects")
                    domain_services = Java("Domain Services")
                
                with Cluster("Application Layer"):
                    use_cases = Java("Use Cases")
                    
                    with Cluster("Input Ports", direction="TB"):
                        component_port = Java("ComponentPort")
                        machine_port = Java("MachinePort")
                        composite_port = Java("CompositePort")
                    
                    with Cluster("Output Ports", direction="TB"):
                        persistence_port = Java("PersistencePort")
                        notification_port = Java("NotificationPort")
                        event_port = Java("EventPublisherPort")
                        file_port = Java("FileSystemPort")
                        cache_port = Java("CachePort")
                
                with Cluster("Adapter Layer"):
                    with Cluster("Input Adapters"):
                        rest_adapter = Java("REST Adapter")
                        cli_adapter = Java("CLI Adapter")
                        messaging_adapter = Java("Messaging Adapter")
                    
                    with Cluster("Output Adapters"):
                        persistence_adapter = Java("PersistenceAdapter")
                        notification_adapter = Java("NotificationAdapter")
                        event_adapter = Java("EventAdapter")
                        file_adapter = Java("FileSystemAdapter")
                        cache_adapter = Java("CacheAdapter")
                
                with Cluster("Infrastructure Layer"):
                    rest_framework = Spring("Spring Framework")
                    database = PostgreSQL("PostgreSQL")
                    file_system = Storage("File System")
                    message_broker = Kafka("Message Broker")
                
                # Domain relationships
                entities >> value_objects
                domain_services >> entities
                
                # Application layer connections
                use_cases >> domain_services
                use_cases >> entities
                
                # Input ports to use cases
                component_port >> use_cases
                machine_port >> use_cases
                composite_port >> use_cases
                
                # Use cases to output ports 
                use_cases >> persistence_port
                use_cases >> notification_port
                use_cases >> event_port
                use_cases >> file_port
                use_cases >> cache_port
                
                # Input adapters to ports
                component_port << rest_adapter
                component_port << cli_adapter
                machine_port << messaging_adapter
                
                # Output ports to adapters
                persistence_port << persistence_adapter
                notification_port << notification_adapter
                event_port << event_adapter
                file_port << file_adapter
                cache_port << cache_adapter
                
                # Infrastructure connections
                rest_adapter >> rest_framework
                persistence_adapter >> database
                file_adapter >> file_system
                event_adapter >> message_broker
                notification_adapter >> message_broker
            
            print(f"Generated Clean Architecture ports diagram: {output_file}")
            return str(output_file)
        
        except Exception as e:
            print(f"Failed to generate Clean Architecture ports diagram: {e}")
            return ""
    
    def generate_all(self) -> list:
        """Generate all port interface diagrams.
        
        Returns:
            List of paths to generated diagrams
        """
        files = []
        
        # Ports component diagram
        ports_component_file = self.generate_ports_component_diagram()
        if ports_component_file:
            files.append(ports_component_file)
        
        # Ports integration diagram
        ports_integration_file = self.generate_ports_integration_diagram()
        if ports_integration_file:
            files.append(ports_integration_file)
        
        # Detailed port diagram
        detailed_port_file = self.generate_detailed_port_diagram()
        if detailed_port_file:
            files.append(detailed_port_file)
        
        # Clean Architecture ports diagram
        clean_arch_ports_file = self.generate_clean_architecture_ports_diagram()
        if clean_arch_ports_file:
            files.append(clean_arch_ports_file)
        
        return files


def main():
    """Main entry point for the script."""
    parser = argparse.ArgumentParser(description="Generate port interface diagrams for Samstraumr")
    parser.add_argument(
        "--output", 
        choices=["png", "svg", "pdf"],
        default="svg",
        help="Output format (default: svg)"
    )
    parser.add_argument(
        "--detail",
        choices=["low", "medium", "high"],
        default="medium",
        help="Detail level for diagrams (default: medium)"
    )
    parser.add_argument(
        "--dir",
        help="Output directory (default: docs/diagrams)"
    )
    
    args = parser.parse_args()
    
    # Initialize generator
    generator = PortInterfaceDiagramGenerator(
        output_dir=args.dir or "docs/diagrams",
        output_format=args.output,
        detail_level=args.detail
    )
    
    # Generate all diagrams
    generated_files = generator.generate_all()
    
    if generated_files:
        print(f"Generated {len(generated_files)} port interface diagrams")
    else:
        print("Failed to generate port interface diagrams")


if __name__ == "__main__":
    main()