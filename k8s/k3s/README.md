# K3S Development Environment Setup 


References:


https://multipass.run/
https://github.com/rancher/k3s
https://medium.com/better-programming/local-k3s-cluster-made-easy-with-multipass-108bf6ce577c


## Single Node Setup

1. Install Multipass
1. Start a virtual machine, ie one-node
   ```
    multipass launch -n one-node -d 20G -m 8G
    ```
2. Install k3s
    ```
    multipass shell one-node
    bash -c "curl -sfL https://get.k3s.io | sh -"

    ```
3. Get kubectl config
    ```
    multipass exec one-node sudo cat /etc/rancher/k3s/k3s.yaml > k3s.yaml
    ```
4. Get ip address of node, ie one-node, from multipass list
5. Add ip address to k3s.yaml
6. set kubectl context
    ```
    cd repo_dir/k8s;
    export PATH="$PATH:$HOME/kubectl";
    export KUBECONFIG=repo_dir/k8s/k3s.yaml
    ```
7. Test.
    ```
    kubectl get pods
    ```


## 3 Node Setup


1. Create cluster
    ```
    multipass launch -n node1 -d 20G -m 8G
    multipass launch -n node2 -d 20G -m 8G
    multipass launch -n node3 -d 20G -m 8G
    ```
1. Install k3s on master node1
    ```
    curl -sfL https://get.k3s.io | sh -
    ```
 1. Get token from node1
    ```
    TOKEN=$(multipass exec node1 sudo cat /var/lib/rancher/k3s/server/node-token)
    ```
1. Get IP of node1
    ```
    IP=$(multipass info node1 | grep IPv4 | awk '{print $2}')
    ```
1. Create k3s worker node2
    ```
    multipass exec node2 -- \
    bash -c "curl -sfL https://get.k3s.io | K3S_URL=\"https://$IP:6443\" K3S_TOKEN=\"$TOKEN\" sh -"
    ```
1. Create k3s worker node3
    ```
    multipass exec node3 -- \
    bash -c "curl -sfL https://get.k3s.io | K3S_URL=\"https://$IP:6443\" K3S_TOKEN=\"$TOKEN\" sh -"
    ```
1. Check cluster
    ```
    multipass exec node1 -- sudo kubectl get nodes
    ```
1. Get kubectl context
    ```
    multipass exec node1 sudo cat /etc/rancher/k3s/k3s.yaml > k3s.yaml
    ### change host reference to node1 IP
    sed -i '' "s/127.0.0.1/$IP/" k3s.yaml
    ### set so we can use local kubectl
    export KUBECONFIG=$PWD/k3s.yaml
    ```
1. Test 
    ```
    kubectl get nodes
    ```